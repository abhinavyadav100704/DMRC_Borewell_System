"use client"

import { useStations, useBorewells, useAuthorities } from "@/hooks/use-data"
import { StatsCards } from "@/components/dashboard/stats-cards"
import { DashboardCharts } from "@/components/dashboard/dashboard-charts"
import { RecentActivity } from "@/components/dashboard/recent-activity"
import { Skeleton } from "@/components/ui/skeleton"

export default function DashboardPage() {
  const { stations, isLoading: stationsLoading } = useStations()
  const { borewells, isLoading: borewellsLoading } = useBorewells()
  const { authorities, isLoading: authoritiesLoading } = useAuthorities()

  const isLoading = stationsLoading || borewellsLoading || authoritiesLoading

  if (isLoading) {
    return (
      <div className="flex flex-col gap-6">
        <div>
          <Skeleton className="mb-1 h-7 w-48" />
          <Skeleton className="h-4 w-72" />
        </div>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {Array.from({ length: 5 }).map((_, i) => (
            <Skeleton key={i} className="h-28 rounded-xl" />
          ))}
        </div>
        <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
          <Skeleton className="h-80 rounded-xl" />
          <Skeleton className="h-80 rounded-xl" />
        </div>
      </div>
    )
  }

  const activeBorewells = borewells.filter((bw) => bw.isAvailable === true).length
  const inactiveBorewells = borewells.filter((bw) => bw.isAvailable === false).length

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-foreground">Dashboard Overview</h1>
        <p className="text-sm text-muted-foreground">
          Monitor borewell operations across DMRC metro stations
        </p>
      </div>

      <StatsCards
        totalStations={stations.length}
        totalBorewells={borewells.length}
        activeBorewells={activeBorewells}
        inactiveBorewells={inactiveBorewells}
        totalAuthorities={authorities.length}
      />

      <DashboardCharts borewells={borewells} stations={stations} />

      <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
        <RecentActivity />
      </div>
    </div>
  )
}
