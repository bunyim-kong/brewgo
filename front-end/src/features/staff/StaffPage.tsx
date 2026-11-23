import { useState, type FormEvent } from 'react'
import { Link, Navigate } from 'react-router-dom'
import RequestError from '../../components/ui/RequestError'
import { ApiError, postForm } from '../../lib/api/client'
import { useResource } from '../../lib/api/useResource'

type Role = 'ADMIN' | 'CASHIER' | 'BARISTA'
type Staff = { id: number; name: string; role: Role; branchId: number | null }

const workspaces: Record<Role, { path: string; title: string }> = {
  ADMIN: { path: '/admin', title: 'Administration' },
  CASHIER: { path: '/staff/cashier', title: 'Cashier workspace' },
  BARISTA: { path: '/staff/barista', title: 'Barista workspace' },
}

export default function StaffPage({ requiredRole }: { requiredRole?: Role }) {
  const session = useResource<Staff>('/auth/me')
  const [busy, setBusy] = useState(false)
  const [message, setMessage] = useState('')

  async function changeSession(path: string, fields?: Record<string, string>) {
    setBusy(true)
    setMessage('')
    try {
      await postForm(path, fields)
      session.reload()
    } catch (error) {
      setMessage(
        error instanceof ApiError && error.status === 401
          ? 'Check your email and password.'
          : 'The request failed. Please try again.',
      )
    } finally {
      setBusy(false)
    }
  }

  function signIn(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const fields = new FormData(event.currentTarget)
    void changeSession('/auth/login', {
      email: String(fields.get('email')),
      password: String(fields.get('password')),
    })
  }

  if (!session.resource) {
    return (
      <p className="container-shell page-section" role="status">
        Checking staff session…
      </p>
    )
  }
  if (session.resource.error && session.resource.error.status !== 401) {
    return (
      <section className="container-shell page-section">
        <h1>Staff sign in</h1>
        <RequestError
          message="Staff access is unavailable right now."
          retry={session.reload}
        />
      </section>
    )
  }

  const staff = session.resource.data
  if (!staff && requiredRole) return <Navigate to="/staff/login" replace />
  if (staff && !requiredRole)
    return <Navigate to={workspaces[staff.role].path} replace />

  return (
    <section className="container-shell page-section staff-section">
      {!staff ? (
        <>
          <h1>Staff sign in</h1>
          <form className="surface grid gap-4 p-6" onSubmit={signIn}>
            <label className="field">
              Email
              <input
                type="email"
                name="email"
                autoComplete="username"
                maxLength={254}
                required
              />
            </label>
            <label className="field">
              Password
              <input
                type="password"
                name="password"
                autoComplete="current-password"
                required
              />
            </label>
            {message && (
              <p className="error-text" role="alert">
                {message}
              </p>
            )}
            <button
              className="button button-primary"
              type="submit"
              disabled={busy}
            >
              {busy ? 'Signing in…' : 'Sign in'}
            </button>
          </form>
        </>
      ) : (
        <>
          <h1>
            {staff.role === requiredRole
              ? workspaces[staff.role].title
              : 'Access denied'}
          </h1>
          {staff.role === requiredRole ? (
            <p>Signed in as {staff.name}.</p>
          ) : (
            <Link
              className="button button-outline"
              to={workspaces[staff.role].path}
            >
              Open my workspace
            </Link>
          )}
          {message && (
            <p className="error-text" role="alert">
              {message}
            </p>
          )}
          <button
            className="button button-outline justify-self-start"
            type="button"
            disabled={busy}
            onClick={() => void changeSession('/auth/logout')}
          >
            Sign out
          </button>
        </>
      )}
    </section>
  )
}
