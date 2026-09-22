$ErrorActionPreference = "Stop"
$raiz = Split-Path -Parent $PSScriptRoot
$pg = Join-Path $env:LOCALAPPDATA "ampera-postgres"
function Escutando([int] $porta) {
    [bool](Get-NetTCPConnection -LocalPort $porta -State Listen -ErrorAction SilentlyContinue)
}

if (Escutando 5432) {
    Write-Host "PostgreSQL ja esta na porta 5432"
} elseif (Test-Path "$pg\pgsql\bin\pg_ctl.exe") {
    & "$pg\pgsql\bin\pg_ctl.exe" -D "$pg\data" -l "$pg\postgres.log" -o "-p 5432" start | Out-Null
    Write-Host "PostgreSQL iniciado"
} else {
    throw "PostgreSQL nao encontrado em $pg. Rode scripts\instalar-local.ps1"
}