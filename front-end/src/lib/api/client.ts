const API_ROOT = '/api'

export class ApiError extends Error {
  readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

async function request(path: string, options?: RequestInit) {
  const response = await fetch(`${API_ROOT}${path}`, options)
  if (!response.ok) {
    throw new ApiError(response.status, 'The request could not be completed.')
  }
  return response
}

export async function getJson<T>(
  path: string,
  signal?: AbortSignal,
): Promise<T> {
  return (await request(path, { signal })).json()
}

export async function postForm(path: string, fields?: Record<string, string>) {
  const session = await fetch(`${API_ROOT}/auth/me`)
  const token = session.headers.get('X-CSRF-TOKEN')
  if (!token)
    throw new ApiError(
      session.status,
      'Could not obtain a staff session token.',
    )
  await request(path, {
    method: 'POST',
    headers: { 'X-CSRF-TOKEN': token },
    body: fields ? new URLSearchParams(fields) : undefined,
  })
}
