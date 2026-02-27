using CatalogService.Events;
using CatalogService.Services;
using Confluent.Kafka;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using System.Text.Json;
using System.Threading;
using System.Threading.Tasks;

namespace CatalogService.Consumers
{
    public class EventConsumer : BackgroundService
    {
        private readonly InventoryReservationService _reservationService;
        private readonly InventoryReleaseService _releaseService;
        private readonly ILogger<EventConsumer> _logger;

        private readonly ConsumerConfig _config = new()
        {
            BootstrapServers = "kafka-broker:29092",
            GroupId = "catalog-service",
            AutoOffsetReset = AutoOffsetReset.Earliest
        };

        public EventConsumer(
            InventoryReservationService reservationService,
            InventoryReleaseService releaseService,
            ILogger<EventConsumer> logger)
        {
            _reservationService = reservationService;
            _releaseService = releaseService;
            _logger = logger;
        }

        protected override Task ExecuteAsync(CancellationToken stoppingToken)
        {
            return Task.Run(() =>
            {
                try
                {
                    StartConsumer(stoppingToken);
                }
                catch (System.Exception ex)
                {
                    _logger.LogError(ex, "EventConsumer background task failed");
                    throw;
                }
            }, stoppingToken);
        }

        private void StartConsumer(CancellationToken stoppingToken)
        {
            using var consumer = new ConsumerBuilder<string, string>(_config).Build();
            consumer.Subscribe(new[]
            {
                Topics.RESERVE_INVENTORY,
                Topics.RELEASE_INVENTORY
            });

            try
            {
                while (!stoppingToken.IsCancellationRequested)
                {
                    var cr = consumer.Consume(stoppingToken);
                    _logger.LogInformation("EventConsumer > Consumed topic: {Topic}, key: {Key}, value: {Value}", cr.Topic, cr.Message.Key, cr.Message.Value);

                    switch (cr.Topic)
                    {
                        case Topics.RESERVE_INVENTORY:
                            var reservationMsg = JsonSerializer.Deserialize<InventoryReservationRequested>(
                                cr.Message.Value,
                                new JsonSerializerOptions { PropertyNameCaseInsensitive = true }
                            );
                            if (reservationMsg != null)
                                _reservationService.HandleAsync(reservationMsg).GetAwaiter().GetResult();
                            break;

                        case Topics.RELEASE_INVENTORY:
                            var releaseMsg = JsonSerializer.Deserialize<InventoryReleaseRequested>(
                                cr.Message.Value,
                                new JsonSerializerOptions { PropertyNameCaseInsensitive = true }
                            );
                            if (releaseMsg != null)
                                _releaseService.HandleAsync(releaseMsg).GetAwaiter().GetResult();
                            break;
                    }
                }
            }
            catch (OperationCanceledException) { }
            catch (System.Exception ex)
            {
                _logger.LogError(ex, "Unhandled exception in EventConsumer.StartConsumer");
                throw;
            }
            finally
            {
                consumer.Close();
            }
        }
    }
}
