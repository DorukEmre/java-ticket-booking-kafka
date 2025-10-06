import { useState, useEffect } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import axios from 'axios';
import './App.css'
// import { getCookie } from './js/utils';
// import CSRFToken from './components/CSRFToken';

function App() {
  const [count, setCount] = useState(0)
  // const [randomNumber, setRandomNumber] = useState(0)
  const [allVenues, setAllVenues] = useState([]);

  let nodeenv = process.env.NODE_ENV;
  const baseURL = import.meta.env.VITE_API_BASE_URL || '';


  useEffect(() => {
    let url = `${baseURL}`;
    console.log("axios get url: ", url);
    axios.get(url, { withCredentials: true })
      .then(response => {
        console.log("response.data: ", response.data);
        setAllVenues(response.data);
      })
      .catch(error => {
        console.error('There was an error making the request', error);
      });
  }, []);

  const getVenues = () => {
    let url = `${baseURL}/catalog/venues`;
    console.log("axios get url: ", url);
    axios.get(url, { withCredentials: true })
      .then(response => {
        console.log("response.data: ", response.data);
        setAllVenues(response.data);
      })
      .catch(error => {
        console.error('There was an error making the request', error);
      });
  }

  const addVenue = async (e) => {
    e.preventDefault();
    let url = `${baseURL}/catalog/add-venue`;
    const formData = new FormData(e.target);

    // Convert FormData to JSON
    const data = {
      name: formData.get('venueName'),
      location: formData.get('venueLocation'),
      totalCapacity: Number(formData.get('venueCapacity'))
    };

    console.log("axios post url: ", url, " data: ", data);
    axios.post(url, data, {
      withCredentials: true,
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => {
        console.log('Call made successfully:', response.data);
      })
      .catch(error => {
        console.error('Error making call:', error);
      });
  }

  return (
    <>
      <div>
        <a href="https://vite.dev" target="_blank">
          <img src={viteLogo} className="logo" alt="Vite logo" />
        </a>
        <a href="https://react.dev" target="_blank">
          <img src={reactLogo} className="logo react" alt="React logo" />
        </a>
      </div>
      <h1>You are in {nodeenv}</h1>
      <div className="card">
        <button onClick={() => setCount((count) => count + 1)}>
          count is {count}
        </button>
      </div>
      <div className="card">
        <button onClick={getVenues}>
          Display all venues:
        </button>
        {allVenues.length > 0 ? (
          <ul>
            {allVenues.map(event => (
              <li key={event.venueId}>
                Venue ID {event.venueId}: {event.name}, {event.location} - {event.totalCapacity}
              </li>
            ))}
          </ul>
        ) : (
          <p>No venues to display.</p>
        )}
      </div>
      <form onSubmit={addVenue}>
        <input type="text" name="venueName" placeholder="Name" />
        <input type="text" name="venueLocation" placeholder="Location" />
        <input type="text" name="venueCapacity" placeholder="Capacity" />
        <button type="submit">Add</button>
      </form>
    </>
  )
}

export default App
