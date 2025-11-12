```
ssh -i "~/.ssh/booking-keypair.pem" ubuntu@ec2-50-16-82-229.compute-1.amazonaws.com
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

## Add swap file (virtual memory)

```
ubuntu:~$ free -h
               total        used        free      shared  buff/cache   available
Mem:           1.9Gi       1.4Gi       217Mi       3.2Mi       460Mi       478Mi
Swap:             0B          0B          0B

ubuntu:~$ sudo fallocate -l 4G /swapfile

ubuntu:~$ sudo chmod 600 /swapfile

ubuntu:~$ sudo mkswap /swapfile
Setting up swapspace version 1, size = 4 GiB (4294963200 bytes)
no label, UUID=...

ubuntu:~$ sudo swapon /swapfile

ubuntu:~$ echo '/swapfile swap swap defaults 0 0' | sudo tee -a /etc/fstab
/swapfile swap swap defaults 0 0

ubuntu:~$ free -h
               total        used        free      shared  buff/cache   available
Mem:           1.9Gi       1.4Gi       209Mi       3.2Mi       463Mi       473Mi
Swap:          4.0Gi          0B       4.0Gi

ubuntu:~$ sudo swapon --show
NAME      TYPE SIZE USED PRIO
/swapfile file   4G   0B   -2
```
