using CatalogService.Events;
using CatalogService.Services;
using Confluent.Kafka;
using Microsoft.Extensions.Hosting;
using System.Text.Json;
using System.Threading;
using System.Threading.Tasks;

namespace CatalogService.Consumers
{
    public class EventConsumer : BackgroundService
    {
        private readonly InventoryReservationService _reservationService;
        private readonly InventoryReleaseService _releaseService;

        private readonly ConsumerConfig _config = new()
        {
            BootstrapServers = "kafka-broker:29092",
            GroupId = "catalog-service",
            AutoOffsetReset = AutoOffsetReset.Earliest
        };

        public EventConsumer(
            InventoryReservationService reservationService,
            InventoryReleaseService releaseService)
        {
            _reservationService = reservationService;
            _releaseService = releaseService;
        }

        protected override Task ExecuteAsync(CancellationToken stoppingToken)
        {
            Task.Run(() => StartConsumer(stoppingToken), stoppingToken);
            return Task.CompletedTask;
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
                    Console.WriteLine($"Consumed topic: {cr.Topic}, key: {cr.Message.Key}, value: {cr.Message.Value}");

                    switch (cr.Topic)
                    {
                        case Topics.RESERVE_INVENTORY:
                            var reservationMsg = JsonSerializer.Deserialize<InventoryReservationRequested>(cr.Message.Value);
                            if (reservationMsg != null)
                                _reservationService.HandleAsync(reservationMsg).GetAwaiter().GetResult();
                            break;

                        case Topics.RELEASE_INVENTORY:
                            var releaseMsg = JsonSerializer.Deserialize<InventoryReleaseRequested>(cr.Message.Value);
                            if (releaseMsg != null)
                                _releaseService.HandleAsync(releaseMsg).GetAwaiter().GetResult();
                            break;
                    }
                }
            }
            catch (OperationCanceledException) { }
            finally
            {
                consumer.Close();
            }
        }
    }
}
