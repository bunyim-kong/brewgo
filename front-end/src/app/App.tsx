import { useEffect, useRef } from 'react'
import { Link, Outlet, Route, Routes, useLocation } from 'react-router-dom'
import HomePage from './HomePage'
import MenuPage from '../features/catalog/MenuPage'
import ProductPage from '../features/catalog/ProductPage'
import StaffPage from '../features/staff/StaffPage'

const navigation = [
  { title: 'Home', to: '/', icon: 'home' },
  { title: 'Menu', to: '/menu', icon: 'menu' },
  { title: 'Staff', to: '/staff/login', icon: 'staff' },
] as const

function Layout() {
  const { pathname } = useLocation()
  const main = useRef<HTMLElement>(null)
  const previousPath = useRef(pathname)
  const destination =
    pathname === '/'
      ? '/'
      : pathname === '/menu' || pathname.startsWith('/products/')
        ? '/menu'
        : pathname.startsWith('/staff/') || pathname === '/admin'
          ? '/staff/login'
          : undefined

  useEffect(() => {
    if (previousPath.current === pathname) return
    previousPath.current = pathname
    window.scrollTo(0, 0)
    main.current?.focus({ preventScroll: true })
  }, [pathname])

  return (
    <>
      <a className="skip-link" href="#main">
        Skip to content
      </a>
      <header className="site-header">
        <div className="container-shell header-inner">
          <Link className="wordmark" to="/">
            <span className="brand-mark" aria-hidden="true">
              <span className="glyph glyph-cup" />
            </span>
            <span>
              BrewGo<span className="wordmark-dot">.</span>
            </span>
          </Link>
          <nav aria-label="Main navigation" className="site-nav">
            {navigation.map(({ title, to, icon }) => (
              <Link
                key={to}
                className="nav-link"
                to={to}
                aria-current={destination === to ? 'page' : undefined}
              >
                <span className={`glyph glyph-${icon}`} aria-hidden="true" />
                {title}
              </Link>
            ))}
          </nav>
        </div>
      </header>
      <main ref={main} id="main" tabIndex={-1} className="grow">
        <Outlet />
      </main>
      <footer className="container-shell site-footer">
        <p>BrewGo Coffee Bar</p>
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
