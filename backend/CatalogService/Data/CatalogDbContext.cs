using Microsoft.EntityFrameworkCore;

using CatalogService.Entities;


namespace CatalogService.Data;

public class CatalogDbContext(DbContextOptions<CatalogDbContext> options)
    : DbContext(options)
{
    public DbSet<Event> Events { get; set; }
    public DbSet<Venue> Venues { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Event>().ToTable("event");
        modelBuilder.Entity<Venue>().ToTable("venue");

        modelBuilder.Entity<Venue>()
            .HasMany<Event>("Events")
            .WithOne(e => e.Venue)
            .HasForeignKey("VenueId")
            .OnDelete(DeleteBehavior.Restrict);
    }
}