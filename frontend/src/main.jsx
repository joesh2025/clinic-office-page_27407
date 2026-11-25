import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import OfficeList from './component/officelist.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <OfficeList />
  </StrictMode>,
)