"use client"

import { stationApi } from "@/lib/api"
import { useState, useMemo } from "react"
import { Plus, Search, Pencil, Trash2, MapPin } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Skeleton } from "@/components/ui/skeleton"
import { useStations, useBorewells } from "@/hooks/use-data"
import type { Station } from "@/lib/api"

export default function StationsPage() {
  const { stations, isLoading, mutate } = useStations()
  const { borewells } = useBorewells()
  const [search, setSearch] = useState("")
  const [dialogOpen, setDialogOpen] = useState(false)
  const [deleteOpen, setDeleteOpen] = useState(false)
  const [editingStation, setEditingStation] = useState<Station | null>(null)
  const [deletingStation, setDeletingStation] = useState<Station | null>(null)

  const [formData, setFormData] = useState({
    stationName: "",
    lineId: 0,
    location: "",
    platformCount: 0,
    openingDate: "",
    stationType: "",
    lastMaintenanceDate: "",
    maintenanceNotes: ""
  })

  // Filter stations
  const filtered = useMemo(() => {
    if (!search) return stations
    const q = search.toLowerCase().trim()
    return stations.filter((s) => 
      (s.stationName?.toLowerCase().includes(q) ?? false) ||
      (s.location?.toLowerCase().includes(q) ?? false) ||
      (s.stationType?.toLowerCase().includes(q) ?? false) ||
      (s.lineId?.toString().includes(q) ?? false)
    )
  }, [stations, search])


  function openAdd() {
    setEditingStation(null)
    setFormData({
      stationName: "",
      lineId: 0,
      location: "",
      platformCount: 0,
      openingDate: "",
      stationType: "",
      lastMaintenanceDate: "",
      maintenanceNotes: ""
    })
    setDialogOpen(true)
  }

  function openEdit(station: Station) {
    setEditingStation(station)
    setFormData({
      stationName: station.stationName,
      lineId: station.lineId || 0,
      location: station.location,
      platformCount: station.platformCount || 0,
      openingDate: station.openingDate || "",
      stationType: station.stationType || "",
      lastMaintenanceDate: station.lastMaintenanceDate || "",
      maintenanceNotes: station.maintenanceNotes || ""
    })
    setDialogOpen(true)
  }

  function openDelete(station: Station) {
    setDeletingStation(station)
    setDeleteOpen(true)
  }

  // Save station
  async function handleSave() {
    try {
      if (editingStation) {
        await stationApi.update(editingStation.stationId, formData)
      } else {
        await stationApi.create(formData)
      }
      await mutate() // refresh stations
      setDialogOpen(false)
      setEditingStation(null)
      setFormData({
        stationName: "",
        lineId: 0,
        location: "",
        platformCount: 0,
        openingDate: "",
        stationType: "",
        lastMaintenanceDate: "",
        maintenanceNotes: "",
      })
    } catch (err: any) {
      const message = err.response?.data?.message || err.message || "Failed to save station"
      alert(message) // or use toast/snackbar
    }
  }

  // Delete station
  async function handleDelete() {
    if (!deletingStation) return
    try {
      await stationApi.delete(deletingStation.stationId)
      await mutate()
      setDeleteOpen(false)
      setDeletingStation(null)
    } catch (err: any) {
      const message = err.response?.data?.message || err.message || "Failed to delete station"
      alert(message)
      // Keep delete dialog open so user can read error
    }
  }

  function getBorewellCount(stationId: number) {
    return borewells.filter((bw) => bw.station?.stationId === stationId).length
  }

  if (isLoading) {
    return (
      <div className="flex flex-col gap-6">
        <Skeleton className="h-7 w-48" />
        <Skeleton className="h-96 rounded-xl" />
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground">Station Management</h1>
          <p className="text-sm text-muted-foreground">
            Manage DMRC metro stations and their borewell assignments
          </p>
        </div>
        <Button onClick={openAdd} className="gap-2">
          <Plus className="size-4" /> Add Station
        </Button>
      </div>

      <Card>
        <CardHeader>
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <CardTitle className="text-base">All Stations</CardTitle>
              <CardDescription>{stations.length} stations registered</CardDescription>
            </div>
            <div className="relative w-full sm:w-72">
              <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                placeholder="Search stations..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="pl-9"
              />
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <div className="w-full overflow-x-auto">
            <Table className="min-w-[1000px]">
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>Name</TableHead>
                <TableHead>Location</TableHead>
                <TableHead>Borewells</TableHead>
                <TableHead>Type</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filtered.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} className="h-32 text-center text-muted-foreground">
                    No stations found
                  </TableCell>
                </TableRow>
              ) : (
                filtered.map((station) => (
                  <TableRow key={station.stationId}>
                    <TableCell className="font-mono text-xs text-muted-foreground">#{station.stationId}</TableCell>
                    <TableCell className="font-medium text-card-foreground">{station.stationName}</TableCell>
                    <TableCell className="flex items-center gap-1.5 text-muted-foreground">
                      <MapPin className="size-3" /> {station.location}
                    </TableCell>
                    <TableCell>
                      <Badge variant="secondary">{getBorewellCount(station.stationId)}</Badge>
                    </TableCell>
                    <TableCell>
                      <Badge variant="outline">{station.stationType || "—"}</Badge>
                    </TableCell>
                    <TableCell className="text-right whitespace-nowrap">
                      <Button variant="ghost" size="icon" onClick={() => openEdit(station)}>
                        <Pencil className="size-3.5" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => openDelete(station)}
                        className="text-destructive hover:text-destructive"
                      >
                        <Trash2 className="size-3.5" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
          </div>
        </CardContent>
      </Card>

      {/* Add/Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editingStation ? "Edit Station" : "Add Station"}</DialogTitle>
            <DialogDescription>
              {editingStation
                ? "Update the station details below."
                : "Fill in all the details to add a new station."}
            </DialogDescription>
          </DialogHeader>
          <div className="flex flex-col gap-4 py-4">
            {/** All fields from Station entity **/}
            <div className="flex flex-col gap-2">
              <Label htmlFor="stationName">Station Name</Label>
              <Input
                id="stationName"
                value={formData.stationName}
                onChange={(e) => setFormData({ ...formData, stationName: e.target.value })}
                placeholder="e.g., Rajiv Chowk"
              />
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="lineId">Line ID</Label>
              <Input
                id="lineId"
                type="number"
                value={formData.lineId}
                onChange={(e) => setFormData({ ...formData, lineId: Number(e.target.value) })}
                placeholder="e.g., 1"
              />
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="location">Location</Label>
              <Input
                id="location"
                value={formData.location}
                onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                placeholder="e.g., Central Delhi"
              />
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="platformCount">Platform Count</Label>
              <Input
                id="platformCount"
                type="number"
                value={formData.platformCount}
                onChange={(e) => setFormData({ ...formData, platformCount: Number(e.target.value) })}
              />
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="openingDate">Opening Date</Label>
              <Input
                id="openingDate"
                type="date"
                value={formData.openingDate}
                onChange={(e) => setFormData({ ...formData, openingDate: e.target.value })}
              />
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="stationType">Station Type</Label>
              <Input
                id="stationType"
                value={formData.stationType}
                onChange={(e) => setFormData({ ...formData, stationType: e.target.value })}
              />
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="lastMaintenanceDate">Last Maintenance Date</Label>
              <Input
                id="lastMaintenanceDate"
                type="date"
                value={formData.lastMaintenanceDate}
                onChange={(e) => setFormData({ ...formData, lastMaintenanceDate: e.target.value })}
              />
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="maintenanceNotes">Maintenance Notes</Label>
              <Input
                id="maintenanceNotes"
                value={formData.maintenanceNotes}
                onChange={(e) => setFormData({ ...formData, maintenanceNotes: e.target.value })}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>
              Cancel
            </Button>
            <Button
              onClick={handleSave}
              disabled={!formData.stationName || !formData.location}
            >
              {editingStation ? "Save Changes" : "Add Station"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation */}
      <AlertDialog open={deleteOpen} onOpenChange={setDeleteOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Station</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete &quot;{deletingStation?.stationName}&quot;? This action cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDelete}
              className="bg-destructive text-white hover:bg-destructive/90"
            >
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
