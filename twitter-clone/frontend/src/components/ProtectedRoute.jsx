import { useAuth0 } from '@auth0/auth0-react'
import { Outlet } from 'react-router-dom'
import { useEffect } from 'react'

export default function ProtectedRoute() {
  const { isAuthenticated, isLoading, loginWithRedirect } = useAuth0()

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      loginWithRedirect()
    }
  }, [isAuthenticated, isLoading, loginWithRedirect])

  if (isLoading) return <div className="loading">Loading…</div>
  if (!isAuthenticated) return null
  return <Outlet />
}
