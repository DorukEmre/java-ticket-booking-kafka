using System;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

using Confluent.Kafka;
using Microsoft.Extensions.Configuration;

namespace CatalogService.Producers;

public class MessageProducer : IDisposable
{
    private readonly IProducer<string, string> _producer;
    private readonly ILogger<MessageProducer> _logger;

    public MessageProducer(
        IConfiguration config,
        ILogger<MessageProducer> logger)
    {
        var producerConfig = new ProducerConfig
        {
            BootstrapServers = Environment.GetEnvironmentVariable("KAFKA_BOOTSTRAP_SERVERS"),
            AllowAutoCreateTopics = true,
            Acks = Acks.All
        };

        _producer = new ProducerBuilder<string, string>(producerConfig).Build();
        _logger = logger;
    }

    public async Task ProduceAsync<T>(string topic, string key, T payloadObj)
    {
        try
        {
            var payload = JsonSerializer.Serialize(payloadObj, new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase });

            var msg = new Message<string, string> { Key = key, Value = payload };

            await _producer.ProduceAsync(topic, msg).ConfigureAwait(false);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "An unexpected error occurred while producing a Kafka message.");
        }
    }

    public void Dispose()
    {
        try
        {
            _producer.Flush(TimeSpan.FromSeconds(5));
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Failed to flush producer before disposal.");
        }

        _producer.Dispose();
    }
}
