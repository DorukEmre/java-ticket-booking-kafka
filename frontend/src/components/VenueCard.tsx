import { Link } from "react-router-dom";

import type { Venue } from "@/types/catalog";
import { imageBaseUrl } from "@/utils/globals";

function VenueCard({ venue, overlayColour }
  : { venue: Venue, overlayColour: { background: string } }) {

  return (
    <>
      <li className="card venue_card__image">
        <img src={`${imageBaseUrl}${venue.imageUrl}`} alt="" className="card-img" />
        <div
          style={overlayColour}
          className="card-img-overlay venue_card__overlay"
          aria-hidden="true"
        />
        <Link to={`/venues/${venue.id}`} className="card-img-overlay d-flex flex-column text-white text-decoration-none align-items-center justify-content-center">
          <p className="fs-3">{venue.name}</p>
          <p className="fs-3">{venue.location}</p>
        </Link>
      </li>
    </>
  )
}

export default VenueCard;