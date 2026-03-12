import { NextRequest, NextResponse } from "next/server"

const BACKEND_URL = process.env.BACKEND_URL
if (!BACKEND_URL) {
  throw new Error("BACKEND_URL is not defined in environment variables")
}

export async function GET(request: NextRequest, { params }: { params: Promise<{ path: string[] }> }) {
  return proxyRequest(request, await params)
}
export async function POST(request: NextRequest, { params }: { params: Promise<{ path: string[] }> }) {
  return proxyRequest(request, await params)
}
export async function PUT(request: NextRequest, { params }: { params: Promise<{ path: string[] }> }) {
  return proxyRequest(request, await params)
}
export async function DELETE(request: NextRequest, { params }: { params: Promise<{ path: string[] }> }) {
  return proxyRequest(request, await params)
}

async function proxyRequest(request: NextRequest, params: { path: string[] }) {
  const path = params.path.join("/")
  const searchParams = request.nextUrl.searchParams.toString()
  const targetUrl = `${BACKEND_URL}/${path}${searchParams ? `?${searchParams}` : ""}`

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    "bypass-tunnel-reminder": "true",
  }

  const authHeader = request.headers.get("Authorization")
  if (authHeader) headers["Authorization"] = authHeader

  let body: string | undefined
  if (request.method !== "GET" && request.method !== "HEAD") {
    try {
      body = await request.text()
    } catch {
      // no body
    }
  }

  try {
    console.log(`[proxy] ${request.method} ${targetUrl}`)

    const response = await fetch(targetUrl, {
      method: request.method,
      headers,
      body,
    })

    const responseText = await response.text()
    const contentType = response.headers.get("content-type") ?? ""

    console.log(`[proxy] ${response.status} — ${responseText.slice(0, 200)}`)

    if (contentType.includes("application/json") && responseText) {
      try {
        return NextResponse.json(JSON.parse(responseText), { status: response.status })
      } catch {
        // fall through
      }
    }

    return new NextResponse(responseText || null, {
      status: response.status,
      headers: { "Content-Type": contentType || "text/plain" },
    })
  } catch (err) {
    console.error(`[proxy] Error →`, err)
    return NextResponse.json(
      { message: `Cannot connect to backend at ${BACKEND_URL}. Is your Spring Boot server running?` },
      { status: 502 }
    )
  }
}