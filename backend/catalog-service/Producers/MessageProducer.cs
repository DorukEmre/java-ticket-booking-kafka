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

    public MessageProducer(IConfiguration config)
    {
        var bootstrap = Environment.GetEnvironmentVariable("KAFKA_BOOTSTRAP_SERVERS");

        var producerConfig = new ProducerConfig
        {
            BootstrapServers = bootstrap
        };

        _producer = new ProducerBuilder<string, string>(producerConfig).Build();
    }

    public async Task ProduceAsync<T>(string topic, string? key, T payloadObj)
    {
        var payload = JsonSerializer.Serialize(payloadObj, new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase });

        var msg = new Message<string, string> { Key = key, Value = payload };

        await _producer.ProduceAsync(topic, msg).ConfigureAwait(false);
    }

    public async Task ProduceAsync<T>(string topic, T payloadObj) =>
        await ProduceAsync(topic, null, payloadObj).ConfigureAwait(false);

    public void Dispose()
    {
        try
        {
            _producer.Flush(TimeSpan.FromSeconds(5));
        }
        catch { /* swallow */ }
        _producer.Dispose();
    }
}
