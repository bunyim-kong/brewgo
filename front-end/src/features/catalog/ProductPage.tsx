import { Link, useLocation, useParams } from 'react-router-dom'
import RequestError from '../../components/ui/RequestError'
import { useResource } from '../../lib/api/useResource'
import { formatPrice, type Product } from './product'
import ProductPhoto from './ProductPhoto'

export default function ProductPage() {
  const { slug = '' } = useParams()
  const { state } = useLocation()
  const { resource, reload } = useResource<Product>(
    `/products/${encodeURIComponent(slug)}`,
  )
  const product = resource?.data

  return (
    <section className="container-shell page-section product-page">
      <Link
        className="button button-outline"
        to={{ pathname: '/menu', search: state?.menuSearch ?? '' }}
      >
        <span aria-hidden="true">←</span> Back to menu
      </Link>
      {!resource && (
        <p className="catalog-status" role="status">
          Loading product…
        </p>
      )}
      {resource?.error?.status === 404 && <h1>Product unavailable</h1>}
      {resource?.error && resource.error.status !== 404 && (
        <RequestError
          message="Product details are unavailable."
          retry={reload}
        />
      )}
      {product && (
        <article className="product-detail">
          <div className="detail-photo">
            <ProductPhoto product={product} detail />
          </div>
          <div className="detail-copy">
            <h1>{product.name}</h1>
            <p className="product-price">{formatPrice(product)}</p>
            {product.description && (
              <p className="detail-description">{product.description}</p>
            )}
            {product.allergens && (
              <p className="allergens">
                <strong>Contains</strong> {product.allergens}
              </p>
            )}
          </div>
        </article>
      )}
    </section>
  )
}
