"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import {
  LayoutDashboard,
  TrainFront,
  Droplets,
  Users,
  LogOut,
  Shield,
} from "lucide-react"
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarSeparator,
} from "@/components/ui/sidebar"
import { useAuth } from "@/lib/auth-context"

const navItems = [
  { title: "Dashboard", href: "/dashboard", icon: LayoutDashboard },
  { title: "Stations", href: "/dashboard/stations", icon: TrainFront },
  { title: "Borewells", href: "/dashboard/borewells", icon: Droplets },
  { title: "Authorities", href: "/dashboard/authorities", icon: Users },
]

export function AppSidebar() {
  const pathname = usePathname()
  const { user, isAdmin, logout } = useAuth()

  return (
    <Sidebar collapsible="icon">
      <SidebarHeader className="p-4">
        <Link href="/dashboard" className="flex items-center gap-3">
          <div className="flex size-8 items-center justify-center rounded-lg bg-sidebar-primary">
            <Droplets className="size-4 text-sidebar-primary-foreground" />
          </div>
          <div className="flex flex-col group-data-[collapsible=icon]:hidden">
            <span className="text-sm font-bold tracking-tight">DMRC</span>
            <span className="text-xs text-sidebar-foreground/60">Borewell Management</span>
          </div>
        </Link>
      </SidebarHeader>
      <SidebarSeparator />
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Navigation</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {navItems.map((item) => (
                <SidebarMenuItem key={item.href}>
                  <SidebarMenuButton
                    asChild
                    isActive={
                      item.href === "/dashboard"
                        ? pathname === "/dashboard"
                        : pathname.startsWith(item.href)
                    }
                    tooltip={item.title}
                  >
                    <Link href={item.href}>
                      <item.icon className="size-4" />
                      <span>{item.title}</span>
                    </Link>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
      <SidebarFooter className="mt-auto">
        <SidebarSeparator />
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton className="group-data-[collapsible=icon]:hidden cursor-default hover:bg-transparent">
              <div className="flex size-7 items-center justify-center rounded-full bg-sidebar-primary/20">
                <span className="text-xs font-semibold text-sidebar-primary">
                  {user?.username?.charAt(0).toUpperCase() || "U"}
                </span>
              </div>
              <div className="flex flex-col">
                <span className="text-sm font-medium">{user?.username || "User"}</span>
                <span className="flex items-center gap-1 text-xs text-sidebar-foreground/60">
                  {isAdmin && <Shield className="size-3" />}
                  {isAdmin ? "Admin" : "User"}
                </span>
              </div>
            </SidebarMenuButton>
          </SidebarMenuItem>
          <SidebarMenuItem>
            <SidebarMenuButton onClick={logout} tooltip="Logout">
              <LogOut className="size-4" />
              <span>Logout</span>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarFooter>
    </Sidebar>
  )
}
