using System.Text.Json;

using Confluent.Kafka;

using CatalogService.Events;
using CatalogService.Services;


namespace CatalogService.Consumers;

public class MessageConsumer(
        InventoryReservationService reservationService,
        InventoryReleaseService releaseService,
        ILogger<MessageConsumer> logger) : BackgroundService
{
    private readonly InventoryReservationService _reservationService = reservationService;
    private readonly InventoryReleaseService _releaseService = releaseService;
    private readonly ILogger<MessageConsumer> _logger = logger;
    private static readonly JsonSerializerOptions JsonOptions = new() { PropertyNameCaseInsensitive = true };

    private readonly ConsumerConfig _config = new()
    {
        BootstrapServers = Environment.GetEnvironmentVariable("KAFKA_BOOTSTRAP_SERVERS"),
        GroupId = "catalog-service",
        AutoOffsetReset = AutoOffsetReset.Earliest
    };


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
                _logger.LogError(ex, "MessageConsumer background task failed");
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
                _logger.LogInformation("MessageConsumer > Consumed topic: {Topic}, key: {Key}, value: {Value}", cr.Topic, cr.Message.Key, cr.Message.Value);

                switch (cr.Topic)
                {
                    case Topics.RESERVE_INVENTORY:
                        var reservationMsg = JsonSerializer.Deserialize<InventoryReservationRequested>(
                            cr.Message.Value, JsonOptions
                        );
                        if (reservationMsg != null)
                            _reservationService.HandleAsync(reservationMsg).GetAwaiter().GetResult();
                        break;

                    case Topics.RELEASE_INVENTORY:
                        var releaseMsg = JsonSerializer.Deserialize<InventoryReleaseRequested>(
                            cr.Message.Value, JsonOptions
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
            _logger.LogError(ex, "Unhandled exception in MessageConsumer.StartConsumer");
            throw;
        }
        finally
        {
            consumer.Close();
        }
    }
}
