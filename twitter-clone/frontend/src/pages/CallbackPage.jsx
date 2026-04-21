import { useAuth0 } from '@auth0/auth0-react'

export default function CallbackPage() {
  const { isLoading } = useAuth0()
  return isLoading ? <div className="loading">Logging in…</div> : null
}
