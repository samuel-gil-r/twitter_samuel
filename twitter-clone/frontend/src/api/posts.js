import axiosInstance from './axiosInstance.js'

export async function getPosts() {
  const { data } = await axiosInstance.get('/api/posts')
  return data
}

export async function createPost(content) {
  const { data } = await axiosInstance.post('/api/posts', { content })
  return data
}
