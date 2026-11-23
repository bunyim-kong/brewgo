import { Link } from 'react-router-dom'
import heroImage from '../assets/images/iced-latte-hero.webp'
import heroImageMobile from '../assets/images/iced-latte-hero-mobile.webp'
import RequestError from '../components/ui/RequestError'
import ProductGrid from '../features/catalog/ProductGrid'
import type { ProductPage } from '../features/catalog/product'
import { useResource } from '../lib/api/useResource'

export default function HomePage() {
  const { resource, reload } = useResource<ProductPage>('/products?size=4')

  return (
    <div className="container-shell home">
      <section className="hero">
        <picture className="hero-media">
          <source media="(max-width: 640px)" srcSet={heroImageMobile} />
          <img
            src={heroImage}
            alt=""
            width={1200}
            height={800}
            fetchPriority="high"
          />
        </picture>
        <div className="hero-copy">
          <h1>
            Make time for <em>good coffee.</em>
          </h1>
          <p>Espresso favorites, iced pours, and fresh pastries.</p>
          <Link className="button button-cream" to="/menu">
            Explore the menu <span aria-hidden="true">↗</span>
          </Link>
        </div>
      </section>
      <section className="featured" aria-labelledby="featured-title">
        <div className="section-heading">
          <h2 id="featured-title">From the menu</h2>
          <Link className="text-link" to="/menu">
            View menu <span aria-hidden="true">↗</span>
          </Link>
        </div>
        {!resource && (
          <p className="catalog-status" role="status">
            Loading menu…
          </p>
        )}
        {resource?.error && (
          <RequestError
            message="The menu is unavailable right now."
            retry={reload}
          />
        )}
        {resource?.data &&
          (resource.data.items.length > 0 ? (
            <ProductGrid products={resource.data.items} />
          ) : (
            <p className="catalog-status">
              No products are available right now.
            </p>
          ))}
      </section>
    </div>
  )
}
