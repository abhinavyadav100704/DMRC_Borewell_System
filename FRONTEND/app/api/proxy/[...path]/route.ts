import { NextRequest, NextResponse } from "next/server"

const BACKEND_URL = process.env.BACKEND_URL!

export async function GET(
  request: NextRequest,
  { params }: { params: Promise<{ path: string[] }> }
) {
  return proxyRequest(request, await params)
}

export async function POST(
  request: NextRequest,
  { params }: { params: Promise<{ path: string[] }> }
) {
  return proxyRequest(request, await params)
}

export async function PUT(
  request: NextRequest,
  { params }: { params: Promise<{ path: string[] }> }
) {
  return proxyRequest(request, await params)
}

export async function DELETE(
  request: NextRequest,
  { params }: { params: Promise<{ path: string[] }> }
) {
  return proxyRequest(request, await params)
}

async function proxyRequest(
  request: NextRequest,
  params: { path: string[] }
) {
  const path = params.path.join("/")
  const targetUrl = `${BACKEND_URL}/${path}`

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    "bypass-tunnel-reminder": "true",
  }

  const authHeader = request.headers.get("Authorization")
  if (authHeader) {
    headers["Authorization"] = authHeader
  }

  let body: string | undefined
  if (request.method !== "GET" && request.method !== "HEAD") {
    try {
      body = await request.text()
    } catch {
      // no body
    }
  }

  try {
    console.log(`[v0] Proxy: ${request.method} ${targetUrl}`)
    if (body) console.log(`[v0] Proxy body: ${body}`)

    const response = await fetch(targetUrl, {
      method: request.method,
      headers,
      body,
    })

    console.log(`[v0] Proxy response status: ${response.status}`)

    const responseText = await response.text()
    console.log(`[v0] Proxy response body: ${responseText}`)

    const contentType = response.headers.get("content-type") || ""

    if (contentType.includes("application/json") && responseText) {
      try {
        const data = JSON.parse(responseText)
        return NextResponse.json(data, { status: response.status })
      } catch {
        return new NextResponse(responseText, {
          status: response.status,
          headers: { "Content-Type": contentType },
        })
      }
    }

    return new NextResponse(responseText || null, {
      status: response.status,
      headers: { "Content-Type": contentType || "text/plain" },
    })
  } catch (err) {
    console.error(`[v0] Proxy error for ${targetUrl}:`, err)
    return NextResponse.json(
      { message: `Cannot connect to backend at ${BACKEND_URL}. Is your Spring Boot server running?` },
      { status: 502 }
    )
  }
}
