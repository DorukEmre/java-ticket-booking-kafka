using Microsoft.EntityFrameworkCore;
using CatalogService.Entities;

namespace CatalogService.Data;

public class CatalogDbContext : DbContext
{
    public CatalogDbContext(DbContextOptions<CatalogDbContext> options)
        : base(options)
    {
    }

    public DbSet<Event> Events { get; set; }
    public DbSet<Venue> Venues { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Venue>()
            .HasMany<Event>("Events")
            .WithOne(e => e.Venue)
            .HasForeignKey("VenueId")
            .OnDelete(DeleteBehavior.Restrict);
    }
}