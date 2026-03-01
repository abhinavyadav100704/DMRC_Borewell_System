"use client"

import useSWR from "swr"
import { stationApi, borewellApi, authorityApi } from "@/lib/api"
import type { Station, Borewell, Authority } from "@/lib/api"

// Generic fetcher wrapper
const stationFetcher = () => stationApi.getAll()
const borewellFetcher = () => borewellApi.getAll()
const authorityFetcher = () => authorityApi.getAll()

export function useStations() {
  const { data, error, mutate } = useSWR<Station[]>("stations", stationFetcher)
  const isLoading = !data && !error
  return { stations: data || [], error, isLoading, mutate }
}

export function useBorewells() {
  const { data, error, mutate } = useSWR<Borewell[]>("borewells", borewellFetcher)
  const isLoading = !data && !error
  return { borewells: data || [], error, isLoading, mutate }
}

export function useAuthorities() {
  const { data, error, mutate } = useSWR<Authority[]>("authorities", authorityFetcher)
  const isLoading = !data && !error
  return { authorities: data || [], error, isLoading, mutate }
}
