import { Link } from 'react-router-dom'
import ProductPhoto from './ProductPhoto'
import { formatPrice, type Product } from './product'

export default function ProductGrid({
  products,
  menuSearch = '',
}: {
  products: Product[]
  menuSearch?: string
}) {
  return (
    <ul className="product-grid">
      {products.map((product) => (
        <li key={product.id}>
          <Link
            className="product-card"
            to={`/products/${encodeURIComponent(product.slug)}`}
            aria-labelledby={`product-${product.id}`}
            state={{ menuSearch }}
          >
            <div className="card-photo">
              <ProductPhoto product={product} />
            </div>
            <div className="card-copy">
              <h3 id={`product-${product.id}`}>{product.name}</h3>
              {product.description && (
                <p className="card-description">{product.description}</p>
              )}
              <div className="card-bottom">
                <p className="product-price">{formatPrice(product)}</p>
                <span className="card-arrow" aria-hidden="true">
                  ↗
                </span>
              </div>
            </div>
          </Link>
        </li>
      ))}
    </ul>
  )
}
