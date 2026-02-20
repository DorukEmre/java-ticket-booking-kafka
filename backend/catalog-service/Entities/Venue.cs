using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace CatalogService.Entities
{
    [Table("venue")]
    public class Venue
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column("id")]
        public long Id { get; set; }

        [Column("name")]
        [Required(ErrorMessage = "Name must not be blank")]
        [StringLength(255, ErrorMessage = "Name must not exceed 255 characters")]
        public string? Name { get; set; } // Nullable

        [Column("location")]
        [Required(ErrorMessage = "Location must not be blank")]
        [StringLength(255, ErrorMessage = "Location must not exceed 255 characters")]
        public string? Location { get; set; }

        [Column("total_capacity")]
        [Range(1, int.MaxValue, ErrorMessage = "Total capacity must be at least 1")]
        public int TotalCapacity { get; set; }

        [Column("image_url")]
        [StringLength(512, ErrorMessage = "Image URL must not exceed 512 characters")]
        public string? ImageUrl { get; set; }

        public Venue() { }

        public Venue(long id, string name, string location, int totalCapacity, string imageUrl)
        {
            Id = id;
            Name = name;
            Location = location;
            TotalCapacity = totalCapacity;
            ImageUrl = imageUrl;
        }
    }
}
