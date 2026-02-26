using Microsoft.EntityFrameworkCore;
using Confluent.Kafka;
using Microsoft.Extensions.Logging;
// using Swashbuckle.AspNetCore.Annotations;
// using Microsoft.OpenApi.Models;
using CatalogService.Services;
using CatalogService.Data; // for CatalogDbContext
using CatalogService.Resources; // for PopulateDatabase on startup

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
// builder.Services.AddEndpointsApiExplorer();
// builder.Services.AddSwaggerGen(c =>
// {
//     // c.SwaggerDoc("v1", new OpenApiInfo { Title = "My API", Version = "v1" });
//     c.EnableAnnotations();
// });

var connectionString
    = builder.Configuration.GetConnectionString("CatalogDb")
    ?? throw new InvalidOperationException("CatalogDb connection string not configured");

// Database Context Configuration
builder.Services.AddDbContext<CatalogDbContext>(options =>
    options.UseMySql(
        connectionString,
        ServerVersion.AutoDetect(connectionString),
        mySqlOptions =>
        {
            mySqlOptions.EnableRetryOnFailure(
            maxRetryCount: 3,
            maxRetryDelay: TimeSpan.FromSeconds(30),
            errorNumbersToAdd: null);
            mySqlOptions.MigrationsAssembly("CatalogService");
        }
    )
);


// Register services
builder.Services.AddScoped<EventService>();
builder.Services.AddScoped<VenueService>();
builder.Services.AddScoped<InventoryService>();
builder.Services.AddScoped<PopulateDatabase>();

// Configure logging
builder.Logging.ClearProviders();
builder.Logging.AddConsole();
builder.Logging.AddDebug();

builder.Services.AddControllers();

var app = builder.Build();

// Apply migrations
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<CatalogDbContext>();
    try
    {
        db.Database.Migrate();
        app.Logger.LogInformation("Database migrated successfully");
    }
    catch (Exception ex)
    {
        app.Logger.LogError(ex, "An error occurred while migrating the database");
    }
}

// Populate database if empty
using (var scope = app.Services.CreateScope())
{
    var seeder = scope.ServiceProvider.GetRequiredService<PopulateDatabase>();
    try
    {
        await seeder.SeedAsync();
    }
    catch (Exception ex)
    {
        app.Logger.LogError(ex, "An error occurred while seeding the database");
    }
}

// Configure the HTTP request pipeline.
// if (app.Environment.IsDevelopment())
// {
//     app.UseSwagger();
//     app.UseSwaggerUI(c => 
//     {
//         c.SwaggerEndpoint("/swagger/v1/swagger.json", "My API V1");
//     });
// }


// Logging incoming requests
app.Use(async (context, next) =>
{
    var logger = app.Services.GetRequiredService<ILogger<Program>>();
    logger.LogInformation(
        "Incoming request: {Method} {Path}",
        context.Request.Method, context.Request.Path);

    await next.Invoke(); // Call the next middleware
});

// Serve static files from wwwroot
app.UseStaticFiles();

app.UseRouting();

app.MapControllers();

app.Run();
