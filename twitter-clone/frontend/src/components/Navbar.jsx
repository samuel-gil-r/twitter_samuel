import { useAuth0 } from '@auth0/auth0-react'
import { Link } from 'react-router-dom'
import { useUser } from '../context/UserContext.jsx'

export default function Navbar() {
  const { isAuthenticated, isLoading, loginWithRedirect, logout } = useAuth0()
  const { profile } = useUser()

  return (
    <nav className="navbar">
      <Link to="/" className="navbar-brand">Twitter Clone</Link>
      <div className="navbar-actions">
        {isLoading ? null : isAuthenticated ? (
          <>
            {profile && (
              <span className="navbar-user">@{profile.username}</span>
            )}
            <Link to="/profile" className="btn btn-secondary">Perfil</Link>
            <button
              className="btn btn-secondary"
              onClick={() => logout({ logoutParams: { returnTo: window.location.origin } })}
            >
              Salir
            </button>
          </>
        ) : (
          <button className="btn btn-primary" onClick={() => loginWithRedirect()}>
            Iniciar sesión
          </button>
        )}
      </div>
    </nav>
  )
}
