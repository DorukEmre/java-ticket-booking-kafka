import type { CSSProperties } from "react";
import { Link } from "react-router-dom";

import type { Venue } from "@/types/catalog";
import { imageBaseUrl } from "@/utils/globals";

function VenueCard({ venue, overlayColour }
  : { venue: Venue, overlayColour: CSSProperties }) {

  return (
    <li className="card venue_card__image">

      <img src={`${imageBaseUrl}${venue.imageUrl}`} alt="Picture of the venue" className="card-img" />

      <div
        style={overlayColour}
        className="card-img-overlay venue_card__overlay"
        aria-hidden="true"
      />

      <Link to={`/venues/${venue.id}`} className="card-img-overlay d-flex flex-column text-white text-decoration-none align-items-center justify-content-center fs-3">
        <p>{venue.name}</p>
        <p>{venue.location}</p>
      </Link>

    </li>
  )
}

export default VenueCard;