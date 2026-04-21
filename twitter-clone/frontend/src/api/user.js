import axiosInstance from './axiosInstance.js'

export async function getCurrentUser() {
  const { data } = await axiosInstance.get('/api/me')
  return data
}

export async function updateDisplayName(displayName) {
  const { data } = await axiosInstance.patch('/api/me', { displayName })
  return data
}
