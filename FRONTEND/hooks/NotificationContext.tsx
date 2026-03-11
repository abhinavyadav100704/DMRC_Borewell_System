"use client"

import { createContext, useContext, useEffect, useState } from "react"
import { connectWebSocket, disconnectWebSocket } from "@/lib/websocket"

interface Notification {
  id: number
  title: string
  message: string
  severity: string
}

interface NotificationContextType {
  notifications: Notification[]
}

const NotificationContext = createContext<NotificationContextType>({
  notifications: [],
})

export const NotificationProvider = ({ children }: { children: React.ReactNode }) => {
  const [notifications, setNotifications] = useState<Notification[]>([])

  useEffect(() => {
    connectWebSocket((newNotification) => {
      setNotifications((prev) => [newNotification, ...prev])
    })

    return () => disconnectWebSocket()
  }, [])

  return (
    <NotificationContext.Provider value={{ notifications }}>
      {children}
    </NotificationContext.Provider>
  )
}

export const useNotifications = () => useContext(NotificationContext)