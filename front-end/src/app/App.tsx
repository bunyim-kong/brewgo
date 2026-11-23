import { Link, NavLink, Outlet, Route, Routes } from 'react-router-dom'
import HomePage from './HomePage'
import MenuPage from '../features/catalog/MenuPage'
import ProductPage from '../features/catalog/ProductPage'
import StaffPage from '../features/staff/StaffPage'

function Layout() {
  return (
    <>
      <a className="skip-link" href="#main">
        Skip to content
      </a>
      <header className="site-header">
        <div className="container-shell flex min-h-18 items-center justify-between gap-3">
          <Link className="wordmark" to="/">
            BrewGo<span>.</span>
          </Link>
          <nav
            aria-label="Main navigation"
            className="flex items-center gap-2 sm:gap-5"
          >
            <NavLink className="nav-link" to="/menu">
              Menu
            </NavLink>
            <NavLink className="nav-link" to="/staff/login">
              Staff
            </NavLink>
          </nav>
        </div>
      </header>
      <main id="main" tabIndex={-1} className="grow">
        <Outlet />
      </main>
      <footer className="container-shell site-footer">
        <p>BrewGo Coffee Bar</p>
        <Link className="nav-link" to="/staff/login">
          Staff sign in
        </Link>
      </footer>
    </>
  )
}

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<HomePage />} />
        <Route path="menu" element={<MenuPage />} />
        <Route path="products/:slug" element={<ProductPage />} />
        <Route path="staff/login" element={<StaffPage />} />
        <Route
          path="staff/cashier"
          element={<StaffPage requiredRole="CASHIER" />}
        />
        <Route
          path="staff/barista"
          element={<StaffPage requiredRole="BARISTA" />}
        />
        <Route path="admin" element={<StaffPage requiredRole="ADMIN" />} />
        <Route
          path="*"
          element={
            <section className="container-shell page-section">
              <h1>Page not found</h1>
              <Link className="button button-primary" to="/">
                Back to BrewGo
              </Link>
            </section>
          }
        />
      </Route>
    </Routes>
  )
}
