import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom'
import { useAuth0 } from '@auth0/auth0-react'
import { useEffect } from 'react'
import Navbar from './components/Navbar.jsx'
import FeedPage from './pages/FeedPage.jsx'
import ProfilePage from './pages/ProfilePage.jsx'
import CallbackPage from './pages/CallbackPage.jsx'
import SetupPage from './pages/SetupPage.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import { setupAxiosInterceptors } from './api/setupInterceptors.js'
import { UserProvider, useUser } from './context/UserContext.jsx'
import './App.css'

function AppRoutes() {
  const { getAccessTokenSilently, isAuthenticated } = useAuth0()
  const { profile, loading } = useUser()

  useEffect(() => {
    setupAxiosInterceptors(getAccessTokenSilently, isAuthenticated)
  }, [getAccessTokenSilently, isAuthenticated])

  const location = useLocation()
  const needsSetup = isAuthenticated && !loading && profile && !profile.profileComplete
  const onSetup = location.pathname === '/setup'

  return (
    <>
      <Navbar />
      <main className="main-container">
        {needsSetup && !onSetup && <Navigate to="/setup" replace />}
        <Routes>
          <Route path="/" element={<FeedPage />} />
          <Route path="/callback" element={<CallbackPage />} />
          <Route path="/setup" element={<SetupPage />} />
          <Route element={<ProtectedRoute />}>
            <Route path="/profile" element={<ProfilePage />} />
          </Route>
        </Routes>
      </main>
    </>
  )
}

function App() {
  return (
    <BrowserRouter>
      <UserProvider>
        <AppRoutes />
      </UserProvider>
    </BrowserRouter>
  )
}

export default App
