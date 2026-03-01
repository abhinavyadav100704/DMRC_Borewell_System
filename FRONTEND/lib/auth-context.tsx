"use client"

import React, { createContext, useContext, useState, useEffect, useCallback } from "react"
import { authApi, type LoginRequest, type RegisterRequest, type JwtResponse } from "./api"

interface User {
  id: number
  username: string
  email: string
  roles: string[]
}

interface AuthContextType {
  user: User | null
  token: string | null
  isAdmin: boolean
  isLoading: boolean
  login: (data: LoginRequest) => Promise<void>
  register: (data: RegisterRequest) => Promise<string>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const storedToken = localStorage.getItem("token")
    const storedUser = localStorage.getItem("user")
    if (storedToken && storedUser) {
      setToken(storedToken)
      setUser(JSON.parse(storedUser))
    }
    setIsLoading(false)
  }, [])

  const login = useCallback(async (data: LoginRequest) => {
    const response: JwtResponse = await authApi.login(data)
    const userData: User = {
      id: response.id,
      username: response.username,
      email: response.email,
      roles: response.roles,
    }
    localStorage.setItem("token", response.token)
    localStorage.setItem("user", JSON.stringify(userData))
    setToken(response.token)
    setUser(userData)
  }, [])

  const register = useCallback(async (data: RegisterRequest) => {
    const response = await authApi.register(data)
    return response.message
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem("token")
    localStorage.removeItem("user")
    setToken(null)
    setUser(null)
  }, [])

  const isAdmin = Array.isArray(user?.roles) && user.roles.includes("ROLE_ADMIN")

  return (
    <AuthContext.Provider value={{ user, token, isAdmin, isLoading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
