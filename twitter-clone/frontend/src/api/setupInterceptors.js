import axiosInstance from './axiosInstance.js'

let requestInterceptorId = null
let responseInterceptorId = null

export function setupAxiosInterceptors(getAccessTokenSilently, isAuthenticated) {
  if (requestInterceptorId !== null) {
    axiosInstance.interceptors.request.eject(requestInterceptorId)
  }
  if (responseInterceptorId !== null) {
    axiosInstance.interceptors.response.eject(responseInterceptorId)
  }

  requestInterceptorId = axiosInstance.interceptors.request.use(async (config) => {
    if (isAuthenticated) {
      try {
        const token = await getAccessTokenSilently({
          authorizationParams: {
            audience: import.meta.env.VITE_AUTH0_AUDIENCE,
            scope: 'openid profile email read:profile write:posts',
          },
        })
        config.headers.Authorization = `Bearer ${token}`
      } catch {
        // Token acquisition failed; request proceeds without auth header
      }
    }
    return config
  })

  responseInterceptorId = axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        window.dispatchEvent(new CustomEvent('auth:unauthorized'))
      }
      return Promise.reject(error)
    }
  )
}
