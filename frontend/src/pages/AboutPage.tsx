import {
  architectureDiagram,
  iconAmazon, iconBootstrap, iconCaddy, iconDocker, iconDotnet, iconGithub, iconJava,
  iconKafka, iconReact, iconSpringBoot, iconTypescript,
} from "@/assets"

function AboutPage() {

  return (
    <div className="d-flex flex-column align-items-center mx-auto"
      style={{ maxWidth: "720px" }}>

      <h1 className="visually-hidden">About</h1>

      <section className="d-flex flex-column pb-5 w-100">
        <h2 className="section__title mb-5">
          Application Architecture
        </h2>
        <div className="p-md-2 align-self-center">
          <img src={architectureDiagram} alt="App architecture diagram" className="rounded" style={{ width: "640px" }} />
        </div>
      </section>

      <section className="d-flex flex-column pb-5 w-100" data-bs-theme="dark">
        <h2 className="section__title mb-5">
          The Tech
        </h2>

        <div className="row mb-5 g-4 justify-content-center mx-auto" style={{ maxWidth: "640px" }} >

          <div className="col-md-6 px-0 px-md-3">
            <div className="card h-100 shadow-sm">
              <h3 className="card-header" style={{ fontSize: "1.25rem" }}>
                Frontend
              </h3>
              <ul className="list-group list-group-flush flex-grow-1">
                <li className="list-group-item">Built with React and TypeScript, styled with Bootstrap</li>
              </ul>
              <div className="card-body flex-grow-0">
                <div className="row">
                  <div className="d-flex justify-content-center align-items-center col">
                    <img src={iconReact} alt="React" height="36" />
                  </div>
                  <div className="d-flex justify-content-center align-items-center col">
                    <img src={iconTypescript} alt="TypeScript" height="36" />
                  </div>
                  <div className="d-flex justify-content-center align-items-center col">
                    <img src={iconBootstrap} alt="Bootstrap" height="36" />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-md-6 px-0 px-md-3">
            <div className="card h-100 shadow-sm">
              <h3 className="card-header" style={{ fontSize: "1.25rem" }}>
                Backend
              </h3>
              <ul className="list-group list-group-flush flex-grow-1">
                <li className="list-group-item">Mixed Java Spring Boot and .NET microservices</li>
                <li className="list-group-item">All services containerised with Docker</li>
              </ul>
              <div className="card-body flex-grow-0">
                <div className="row">
                  <div className="d-flex justify-content-center align-items-center col">
                    <img src={iconJava} alt="Java" height="36" />
                  </div>
                  <div className="d-flex justify-content-center align-items-center col">
                    <img src={iconSpringBoot} alt="Spring Boot" height="36" />
                  </div>
                  <div className="d-flex justify-content-center align-items-center col">
                    <img src={iconDotnet} alt="Dotnet" height="36" />
                  </div>
                  <div className="d-flex justify-content-center align-items-center col">
                    <img src={iconDocker} alt="Docker" height="36" />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-md-6 px-0 px-md-3">
            <div className="card h-100 shadow-sm">
              <h3 className="card-header" style={{ fontSize: "1.25rem" }}>
                Event-Driven Architecture
              </h3>
              <ul className="list-group list-group-flush flex-grow-1">
                <li className="list-group-item">Microservices are decoupled and communicate asynchronously using Kafka events</li>
              </ul>
              <div className="card-body flex-grow-0">
                <div className="row">
                  <div className="d-flex justify-content-center align-items-center col">
                    <img src={iconKafka} alt="Apache Kafka" height="36" />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-md-6 px-0 px-md-3">
            <div className="card h-100 shadow-sm">
              <h3 className="card-header" style={{ fontSize: "1.25rem" }}>
                Deployment & CI/CD
              </h3>
              <ul className="list-group list-group-flush flex-grow-1">
                <li className="list-group-item">Caddy serves the frontend and reverse proxies API requests to an API Gateway</li>
                <li className="list-group-item">Cloud deployment on AWS EC2 with CI/CD via GitHub Actions</li>
              </ul>
              <div className="card-body flex-grow-0">
                <div className="row">
                  <div className="d-flex justify-content-center align-items-center col">
                    <img src={iconCaddy} alt="Caddy" height="36" />
                  </div>
                  <div className="d-flex justify-content-center align-items-center col">
                    <img src={iconAmazon} alt="Amazon Web Services" height="36" />
                  </div>
                  <div className="d-flex justify-content-center align-items-center col">
                    <img src={iconGithub} alt="Github" height="36" />
                  </div>
                </div>
              </div>
            </div>
          </div>

        </div>

      </section>


      <section className="d-flex flex-column pb-5 w-100" data-bs-theme="dark">

        <h2 className="section__title mb-5">
          View the Code
        </h2>

        <div className="d-flex align-items-center">
          <img src={iconGithub} height="28" alt="GitHub" className="me-2" />
          <p className="mb-0">
            Explore the full source code for{" "}
            <a
              href="https://github.com/dorukEmre/java-ticket-booking-kafka"
              target="_blank"
              rel="noreferrer"
              aria-label="View on GitHub"
              title="View on GitHub"
              className="github-link"
            >
              this project on GitHub
            </a>.
          </p>
        </div>

      </section>
    </div>
  )
}

export default AboutPage
