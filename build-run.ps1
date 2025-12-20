$ErrorActionPreference = "Stop"

$projectDirs = @(
    "config-service1"
    "eureka-service",
    "first-module",
    "gateway-service",
    "second-service"
)

foreach ($dir in $projectDirs) {
    if (-not (Test-Path $dir)) {
        continue
    }
    Push-Location $dir
    try {
        ./gradlew.bat clean build
    } finally {
        Pop-Location
    }
    if ($LASTEXITCODE -ne 0) {
        Write-Host "naebnulos' vse, davai po novoi"
        exit 1
    }
}

docker compose down
docker compose build
Start-Sleep -Seconds 5

docker compose up -d config-service
Start-Sleep -Seconds 5

docker compose up -d eureka-service
Start-Sleep -Seconds 5

try {
    Invoke-WebRequest -UseBasicParsing -Uri "http://localhost:8761"
} catch {
    docker compose logs --tail=200 eureka-service
    exit 1
}

docker compose up -d

