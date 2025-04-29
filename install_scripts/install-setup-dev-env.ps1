# Verifica e ajusta política de execução
$currentPolicy = Get-ExecutionPolicy -Scope CurrentUser
if ($currentPolicy -ne 'RemoteSigned') {
    Write-Host "Atualizando política de execução para RemoteSigned..."
    try {
        Set-ExecutionPolicy RemoteSigned -Scope CurrentUser -Force
    } catch {
        Write-Error "Execute este script como administrador para ajustar a política de execução."
        exit 1
    }
}

# Instala Chocolatey se necessário
if (!(Get-Command choco -ErrorAction SilentlyContinue)) {
    Write-Host "Instalando Chocolatey..."
    Set-ExecutionPolicy Bypass -Scope Process -Force
    try {
        [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12
        iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
        $env:PATH += ";$env:ALLUSERSPROFILE\chocolatey\bin"
        [System.Environment]::SetEnvironmentVariable("Path", $env:PATH, "User") # Tornar a alteração permanente para o usuário
    } catch {
        Write-Error "Erro ao instalar Chocolatey: $($_.Exception.Message)"
        exit 1
    }
}

# Atualiza Chocolatey
Write-Host "Atualizando Chocolatey..."
choco upgrade chocolatey -y

# Instala dependências
Write-Host "Instalando dependências via Chocolatey..."
choco install openjdk17 -y
choco install python --version=3.12.0 -y
choco install nodejs -y
choco install maven -y

# Adiciona permanentemente os binários ao PATH
Write-Host "Adicionando binários ao PATH..."
$javaBin = "$env:ProgramFiles\Java\jdk-17\bin"
$nodeBin = "$env:ProgramFiles\nodejs"
$pythonBin = "$env:ProgramFiles\Python312\Scripts;$env:ProgramFiles\Python312"

if (-not $env:PATH.Contains($javaBin)) {
    $env:PATH += ";$javaBin"
    [System.Environment]::SetEnvironmentVariable("Path", $env:PATH, "User")
}
if (-not $env:PATH.Contains($nodeBin)) {
    $env:PATH += ";$nodeBin"
    [System.Environment]::SetEnvironmentVariable("Path", $env:PATH, "User")
}
if (-not $env:PATH.Contains($pythonBin.Split(';')[0])) { # Verifica apenas o diretório principal de scripts do Python
    $env:PATH += ";$pythonBin"
    [System.Environment]::SetEnvironmentVariable("Path", $env:PATH, "User")
}

# Espera alguns segundos para o sistema processar (pode ser removido, pois as alterações no PATH são imediatas)
# Start-Sleep -Seconds 5

# Instala ferramentas via npm e pip
Write-Host "Instalando ferramentas via npm..."
try { npm install -g jest } catch { Write-Warning "Erro ao instalar Jest: $($_.Exception.Message)" }

Write-Host "Atualizando pip e instalando ferramentas via pip..."
try { pip install --upgrade pip } catch { Write-Warning "Erro ao atualizar pip: $($_.Exception.Message)" }
try { pip install poetry mkdocs mkdocs-material mkdocs-simple-plugin plantuml-markdown mkdocs-mermaid2-plugin mkdocs_puml } catch { Write-Warning "Erro ao instalar ferramentas pip: $($_.Exception.Message)" }

# Verifica se Ubuntu está instalado no WSL
Write-Host "Verificando instalação do Ubuntu no WSL..."
$wslList = wsl -l -q
if ($wslList -notmatch "Ubuntu") {
    Write-Host "Instalando Ubuntu..."
    wsl --install -d Ubuntu
    Write-Host "Por favor, reinicie o sistema antes de continuar."
    exit 0
}

# Converte Ubuntu para WSL 2 se necessário
Write-Host "Verificando versão do Ubuntu no WSL..."
$ubuntuState = wsl -l -v | Select-String "Ubuntu"
if ($ubuntuState -and $ubuntuState -notmatch "2") {
    Write-Host "Convertendo Ubuntu para WSL 2..."
    wsl --set-version Ubuntu 2
}

# Instala o Docker dentro do WSL
Write-Host "Instalando Docker no Ubuntu via WSL..."
wsl -d Ubuntu -- bash -c "
    sudo apt update &&
    sudo apt install -y apt-transport-https ca-certificates curl gnupg lsb-release &&
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg &&
    echo 'deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \$(lsb_release -cs) stable' | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null &&
    sudo apt update &&
    sudo apt install -y docker-ce docker-ce-cli containerd.io &&
    sudo usermod -aG docker \$(whoami)
"
Write-Host "A instalação do Docker no WSL foi iniciada. Após a conclusão, pode ser necessário sair e entrar novamente no WSL para que as alterações de grupo entrem em vigor."

# Exibe versões instaladas
Write-Host "`n📦 Versões Instaladas:"
try { java -version } catch { Write-Warning "Java não encontrado no PATH." }
try { python --version } catch { Write-Warning "Python não encontrado no PATH." }
try { node --version } catch { Write-Warning "Node.js não encontrado no PATH." }
try { mvn --version } catch { Write-Warning "Maven não encontrado no PATH." }
try { jest --version } catch { Write-Warning "Jest não encontrado." }
try { mkdocs --version } catch { Write-Warning "MkDocs não encontrado." }
try { poetry --version } catch { Write-Warning "Poetry não encontrado." }

Write-Host "`nInstalação concluída! Pode ser necessário reiniciar o terminal ou o sistema para que todas as alterações no PATH entrem em vigor."