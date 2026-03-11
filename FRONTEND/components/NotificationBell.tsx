"use client"

import { useNotifications } from "@/hooks/NotificationContext"
import { Bell } from "lucide-react"
import { useState } from "react"

export default function NotificationBell() {
  const { notifications } = useNotifications()
  const [open, setOpen] = useState(false)

  return (
    <div className="relative">
      <button
        onClick={() => setOpen(!open)}
        className="relative p-2 rounded-full hover:bg-gray-200 transition"
      >
        <Bell className="w-6 h-6 text-gray-700" />

        {notifications.length > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-600 text-white text-xs px-2 py-0.5 rounded-full">
            {notifications.length}
          </span>
        )}
      </button>

      {open && (
        <div className="absolute right-0 mt-2 w-80 bg-white shadow-lg rounded-xl p-4 z-50">
          <h3 className="font-semibold mb-2">Notifications</h3>

          {notifications.length === 0 ? (
            <p className="text-sm text-gray-500">No notifications</p>
          ) : (
            <div className="max-h-60 overflow-y-auto space-y-2">
              {notifications.map((n: any) => (
                <div
                  key={n.id}
                  className="p-2 border rounded-lg text-sm"
                >
                  <p className="font-medium">{n.title}</p>
                  <p className="text-gray-600">{n.message}</p>
                  <p className="text-xs text-gray-400">{n.severity}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  )
}