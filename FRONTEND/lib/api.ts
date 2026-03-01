// All browser requests go through the Next.js proxy to avoid CORS issues.
// The proxy forwards to BACKEND_URL (server-side env var, default http://localhost:8080/api).
const API_BASE_URL = "http://localhost:8080"

async function apiFetch<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const token =
    typeof window !== "undefined" ? localStorage.getItem("token") : null

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string>),
  }

  if (token) {
    headers["Authorization"] = `Bearer ${token}`
  }

  let res: Response
  try {
    res = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers,
    })
  } catch {
    throw new Error(
      "Network error. Please check your connection and try again."
    )
  }

  if (res.status === 401) {
    if (typeof window !== "undefined") {
      localStorage.removeItem("token")
      localStorage.removeItem("user")
      window.location.href = "/login"
    }
    throw new Error("Unauthorized")
  }

  if (!res.ok) {
    let errorMessage = `Request failed (HTTP ${res.status})`
    try {
      const errorBody = await res.json()
      console.log("[v0] API error response:", errorBody)
      // Spring Boot can return { message: "..." } or { error: "..." } or just a string
      errorMessage = errorBody.message || errorBody.error || JSON.stringify(errorBody)
    } catch {
      try {
        const text = await res.text()
        if (text) errorMessage = text
      } catch {
        // use default
      }
    }
    throw new Error(errorMessage)
  }

  // If no content, return undefined
  if (res.status === 204) return undefined as unknown as T
  return res.json()
  
}

// Auth
export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  role?: string
}

export interface JwtResponse {
  token: string
  id: number
  username: string
  email: string
  roles: string[]
  type: string
}

export const authApi = {
  login: (data: LoginRequest) =>
    apiFetch<JwtResponse>("/auth/signin", { method: "POST", body: JSON.stringify(data) }),
  register: (data: RegisterRequest) =>
    apiFetch<{ message: string }>("/auth/signup", { method: "POST", body: JSON.stringify(data) }),
}

// Station
export interface Station {
  stationId: number
  stationName: string
  lineId: number
  location: string
  platformCount: number
  openingDate: string // or Date if you parse it
  stationType: string
  lastMaintenanceDate: string
  maintenanceNotes: string
}

export type StationCreate = Omit<Station, "stationId">
export type StationUpdate = Omit<Station, "stationId">

export const stationApi = {
  getAll: () => apiFetch<Station[]>("/stations"),
  getById: (id: number) => apiFetch<Station>(`/stations/${id}`),
  create: (data: StationCreate) =>
    apiFetch<Station>("/stations", { method: "POST", body: JSON.stringify(data) }),
  update: (id: number, data: StationUpdate) =>
    apiFetch<Station>(`/stations/${id}`, { method: "PUT", body: JSON.stringify(data) }),
  delete: (id: number) =>
    apiFetch<void>(`/stations/${id}`, { method: "DELETE" }),
}

// Borewell

export interface Borewell {
  borewellId: number
  station: Station
  authority?: Authority | null   // 👈 ADD THIS
  borewellNo: number
  isAvailable: boolean
  distanceM?: number
  diameter?: number
  depth: number
  location?: string
  approvalDate?: string | Date
}


export const borewellApi = {
  getAll: () => apiFetch<Borewell[]>("/borewells"),
  getById: (id: number) => apiFetch<Borewell>(`/borewells/${id}`),
  create: (data: Partial<Borewell>) =>
    apiFetch<Borewell>("/borewells", { method: "POST", body: JSON.stringify(data) }),
  update: (id: number, data: Partial<Borewell>) =>
    apiFetch<Borewell>(`/borewells/${id}`, { method: "PUT", body: JSON.stringify(data) }),
  delete: (id: number) =>
    apiFetch<void>(`/borewells/${id}`, { method: "DELETE" }),
}

// Authority
export interface Authority {
  authorityId: number
  name: string
  designation: string
  contactNumber: string
  email: string
}

export type AuthorityCreate = Omit<Authority, "authorityId">

export const authorityApi = {
  getAll: () => apiFetch<Authority[]>("/authorities"),
  getById: (id: number) => apiFetch<Authority>(`/authorities/${id}`),
  create: (data: AuthorityCreate) =>
  apiFetch<Authority>("/authorities", {
    method: "POST",
    body: JSON.stringify(data),
  }),
  update: (id: number, data: AuthorityCreate) =>
    apiFetch<Authority>(`/authorities/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    }),
  delete: (id: number) =>
    apiFetch<void>(`/authorities/${id}`, { method: "DELETE" }),
}
