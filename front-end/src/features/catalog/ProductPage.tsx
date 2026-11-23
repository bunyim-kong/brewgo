import { Link, useLocation, useParams } from 'react-router-dom'
import RequestError from '../../components/ui/RequestError'
import { useResource } from '../../lib/api/useResource'
import { formatPrice, type Product } from './product'

export default function ProductPage() {
  const { slug = '' } = useParams()
  const { state } = useLocation()
  const { resource, reload } = useResource<Product>(
    `/products/${encodeURIComponent(slug)}`,
  )
  const product = resource?.data

  return (
    <section className="container-shell page-section max-w-2xl">
      <Link
        className="button button-outline"
        to={{ pathname: '/menu', search: state?.menuSearch ?? '' }}
      >
        <span aria-hidden="true">←</span> Back to menu
      </Link>
      {!resource && <p role="status">Loading product…</p>}
      {resource?.error?.status === 404 && <h1>Product unavailable</h1>}
      {resource?.error && resource.error.status !== 404 && (
        <RequestError
          message="Product details are unavailable."
          retry={reload}
        />
      )}
      {product && (
        <article className="surface grid gap-4 p-6 sm:p-8">
          <h1>{product.name}</h1>
          {product.description && <p>{product.description}</p>}
          {product.allergens && (
            <p className="allergens">Contains {product.allergens}</p>
          )}
          <p className="product-price">{formatPrice(product)}</p>
        </article>
      )}
    </section>
  )
}
