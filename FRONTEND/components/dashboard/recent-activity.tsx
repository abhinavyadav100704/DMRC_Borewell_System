"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Droplets, TrainFront, Users } from "lucide-react"

const activities = [
  {
    icon: Droplets,
    label: "BW-015 added to Chandni Chowk",
    time: "2 hours ago",
    type: "borewell" as const,
  },
  {
    icon: TrainFront,
    label: "Saket station updated",
    time: "5 hours ago",
    type: "station" as const,
  },
  {
    icon: Users,
    label: "Anita Gupta assigned as Environmental Officer",
    time: "1 day ago",
    type: "authority" as const,
  },
  {
    icon: Droplets,
    label: "BW-009 marked as Inactive",
    time: "2 days ago",
    type: "borewell" as const,
  },
  {
    icon: TrainFront,
    label: "Botanical Garden station added",
    time: "3 days ago",
    type: "station" as const,
  },
]

const typeColors: Record<string, string> = {
  borewell: "bg-chart-2/10 text-chart-2",
  station: "bg-primary/10 text-primary",
  authority: "bg-chart-4/10 text-chart-4",
}

export function RecentActivity() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Recent Activity</CardTitle>
        <CardDescription>Latest operations and changes</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="flex flex-col gap-3">
          {activities.map((activity, i) => (
            <div key={i} className="flex items-center gap-3">
              <div className={`flex size-8 shrink-0 items-center justify-center rounded-lg ${typeColors[activity.type]}`}>
                <activity.icon className="size-4" />
              </div>
              <div className="flex flex-1 flex-col">
                <span className="text-sm text-card-foreground">{activity.label}</span>
                <span className="text-xs text-muted-foreground">{activity.time}</span>
              </div>
              <Badge variant="secondary" className="text-xs capitalize">
                {activity.type}
              </Badge>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}
