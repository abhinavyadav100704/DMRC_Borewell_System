import SockJS from "sockjs-client"
import { Client } from "@stomp/stompjs"

let stompClient: Client | null = null

export const connectWebSocket = (onMessageReceived: (notification: any) => void) => {
  const socket = new SockJS("http://localhost:8080/ws")

  stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    onConnect: () => {
      console.log("✅ Connected to WebSocket")

      stompClient?.subscribe("/topic/notifications", (message) => {
        if (message.body) {
          const notification = JSON.parse(message.body)
          onMessageReceived(notification)
        }
      })
    },
  })

  stompClient.activate()
}

export const disconnectWebSocket = () => {
  stompClient?.deactivate()
}