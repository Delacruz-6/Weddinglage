$ErrorActionPreference = 'Stop'
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession

function CookieValue([string]$name) {
  $c = $session.Cookies.GetCookies('http://localhost') | Where-Object { $_.Name -eq $name } | Select-Object -First 1
  if ($null -eq $c) { return $null }
  return $c.Value
}

function ShowResult([string]$label, $resp) {
  Write-Host ("{0}: {1}" -f $label, $resp.StatusCode)
}

# CSRF bootstrap
$csrfResp = Invoke-WebRequest -Uri 'http://localhost/api/admin/auth/csrf' -WebSession $session -UseBasicParsing
ShowResult 'CSRF' $csrfResp
$xsrf = CookieValue 'XSRF-TOKEN'
Write-Host ("XSRF-TOKEN present: {0}" -f (-not [string]::IsNullOrEmpty($xsrf)))

# Login
$loginBody = '{"email":"admin@example.com","password":"change-me-admin"}'
try {
  $login = Invoke-WebRequest -Uri 'http://localhost/api/admin/auth/login' -Method Post -Body $loginBody -ContentType 'application/json; charset=utf-8' -WebSession $session -Headers @{ 'X-XSRF-TOKEN' = $xsrf } -UseBasicParsing
  ShowResult 'LOGIN' $login
} catch {
  Write-Host ("LOGIN FAIL: {0}" -f $_.Exception.Message)
  if ($_.ErrorDetails) { Write-Host $_.ErrorDetails.Message }
  throw
}
$xsrf = CookieValue 'XSRF-TOKEN'

# Admin GETs
$gets = @(
  '/api/admin/dashboard',
  '/api/admin/configuration',
  '/api/admin/invitations',
  '/api/admin/photos',
  '/api/admin/publication',
  '/api/admin/venue',
  '/api/admin/timeline',
  '/api/admin/playlist',
  '/api/admin/exports/guests'
)
foreach ($path in $gets) {
  try {
    $r = Invoke-WebRequest -Uri ("http://localhost{0}" -f $path) -WebSession $session -Headers @{ 'X-XSRF-TOKEN' = $xsrf } -UseBasicParsing
    ShowResult ("GET {0}" -f $path) $r
  } catch {
    $code = $_.Exception.Response.StatusCode.value__
    Write-Host ("GET {0} FAIL: {1}" -f $path, $code)
    if ($_.ErrorDetails) { Write-Host $_.ErrorDetails.Message }
  }
}

# Public GETs
$publicGets = @(
  '/api/public/wedding',
  '/api/public/venue',
  '/api/public/timeline',
  '/api/public/playlist',
  '/api/public/spotify-playlist',
  '/api/public/photos'
)
foreach ($path in $publicGets) {
  try {
    $r = Invoke-WebRequest -Uri ("http://localhost{0}" -f $path) -UseBasicParsing
    ShowResult ("GET {0}" -f $path) $r
  } catch {
    $code = $_.Exception.Response.StatusCode.value__
    Write-Host ("GET {0} FAIL: {1}" -f $path, $code)
    if ($_.ErrorDetails) { Write-Host $_.ErrorDetails.Message }
  }
}

# PUT configuration
$cfgJson = (Invoke-WebRequest -Uri 'http://localhost/api/admin/configuration' -WebSession $session -Headers @{ 'X-XSRF-TOKEN' = $xsrf } -UseBasicParsing).Content
$cfg = $cfgJson | ConvertFrom-Json
$cfg.partnerNames = 'Smoke Partners'
$cfg.welcomeMessage = 'Hola desde smoke'
$putBody = $cfg | ConvertTo-Json -Depth 6
try {
  $put = Invoke-WebRequest -Uri 'http://localhost/api/admin/configuration' -Method Put -Body $putBody -ContentType 'application/json; charset=utf-8' -WebSession $session -Headers @{ 'X-XSRF-TOKEN' = $xsrf } -UseBasicParsing
  ShowResult 'PUT /api/admin/configuration' $put
  Write-Host $put.Content.Substring(0, [Math]::Min(300, $put.Content.Length))
} catch {
  $code = $_.Exception.Response.StatusCode.value__
  Write-Host ("PUT configuration FAIL: {0}" -f $code)
  if ($_.ErrorDetails) { Write-Host $_.ErrorDetails.Message }
  Write-Host 'BODY SENT:'
  Write-Host $putBody
}
