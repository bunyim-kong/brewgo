import { useEffect, useState } from 'react'
import { ApiError, getJson } from './client'

type Resource<T> =
  { data: T; error?: never } | { error: ApiError; data?: never }

export function useResource<T>(path: string) {
  const [result, setResult] = useState<{
    path: string
    version: number
    resource: Resource<T>
  }>()
  const [version, setVersion] = useState(0)

  useEffect(() => {
    const controller = new AbortController()
    getJson<T>(path, controller.signal)
      .then((data) => {
        if (!controller.signal.aborted)
          setResult({ path, version, resource: { data } })
      })
      .catch((cause: unknown) => {
        if (controller.signal.aborted) return
        const error =
          cause instanceof ApiError
            ? cause
            : new ApiError(0, 'Network unavailable.')
        setResult({ path, version, resource: { error } })
      })
    return () => controller.abort()
  }, [path, version])

  return {
    resource:
      result?.path === path && result.version === version
        ? result.resource
        : undefined,
    reload: () => setVersion((current) => current + 1),
  }
}
