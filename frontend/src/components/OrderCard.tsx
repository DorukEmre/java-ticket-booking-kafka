
import { Link } from "react-router-dom";

import type { OrderResponse } from "@/types/order";

function OrderCard(
  { order, isList }: { order: OrderResponse, isList?: boolean }
) {


  const orderContent = (
    <>
      <p>Status: <span className={order.status === "COMPLETED" ? "bg-success p-2" : "bg-danger p-2"}>{order.status}</span></p>
      <p>Placed At: {new Date(order.placedAt).toLocaleString()}</p>
      {order.status === "COMPLETED" && (
        <p>Total Price:
          <span className="fw-bold">{" "}{order.totalPrice.toFixed(2)}{'\u00A0€'}</span>
        </p>
      )}
      <p>Items:</p>
      <ul>
        {order.items.map((item) => (
          <li key={item.id} className="ms-3">
            <p>
              <span>{item.ticketCount} {item.ticketCount > 1 ? "tickets" : "ticket"} </span>
              <span>for event {item.eventId} </span>
              {order.status === "COMPLETED" && (
                <span>at {item.ticketPrice.toFixed(2)} each</span>
              )}
            </p>
          </li>
        ))}
      </ul>
    </>
  );


  if (isList) {
    return (

      <li key={order.orderId} className="mb-3 p-3 border rounded d-flex flex-column gap-1 ">
        <p>Order ID:{" "}
          <Link to={`/orders/${order.orderId}`} className="text-decoration-none">
            <span className="fw-bold">
              {order.orderId}
            </span>
          </Link>
        </p>

        {orderContent}

      </li>
    )
  }

  return (

    <div className="d-flex flex-column gap-1 mx-auto mt-4">
      <p>Order ID: <span className="fw-bold">{order.orderId}</span></p>

      {orderContent}

    </div>
  )

}

export default OrderCard;