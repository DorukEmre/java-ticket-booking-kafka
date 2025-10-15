```
ssh -i "booking-keypair.pem" ubuntu@ec2-50-16-82-229.compute-1.amazonaws.com
```


### 1️⃣ Remove any old Docker stuff (just in case)

```bash
sudo apt remove docker docker-engine docker.io containerd runc -y
```

### 2️⃣ Set up Docker’s official repo

```bash
sudo apt update
sudo apt install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
```

### 3️⃣ Install Docker Engine **and** the Compose plugin

```bash
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

### 4️⃣ Enable and start Docker

```bash
sudo systemctl enable docker
sudo systemctl start docker
```

### 5️⃣ Allow non-root Docker usage

```bash
sudo usermod -aG docker ubuntu
newgrp docker
```

### 6️⃣ Verify installation

```bash
docker --version
docker compose version
```

```
sudo apt install -y git maven make
sudo apt install -y build-essential
git clone https://github.com/DorukEmre/java-ticket-booking.git
```

```
scp -i booking-keypair.pem ~/repos/java-ticket-booking/.env ubuntu@13.49.183.24:/home/ubuntu/java-ticket-booking/.env
```

Caddyfile:
```
ec2-50-16-82-229.compute-1.amazonaws.com {
    reverse_proxy gatewayapi:8000
    log {
        output stdout
        level WARN
    }
}
```
```
50.16.82.229 {
    reverse_proxy gatewayapi:8000
    log {
        output stdout
        level WARN
    }
}
```