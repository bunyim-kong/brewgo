import { useSearchParams } from 'react-router-dom'
import RequestError from '../../components/ui/RequestError'
import { useResource } from '../../lib/api/useResource'
import ProductGrid from './ProductGrid'
import type { ProductPage } from './product'

type Category = { id: number; name: string }

export default function MenuPage() {
  const [params, setParams] = useSearchParams()
  const query = params.get('q') ?? ''
  const categoryId = params.get('categoryId') ?? ''
  const page = Math.min(
    100_000,
    Math.max(0, Number.parseInt(params.get('page') ?? '0', 10) || 0),
  )
  const apiParams = new URLSearchParams({ page: String(page), q: query })
  if (categoryId) apiParams.set('categoryId', categoryId)

  const categories = useResource<Category[]>('/categories')
  const products = useResource<ProductPage>(`/products?${apiParams}`)

  function updateFilters(updates: Record<string, string>) {
    const next = new URLSearchParams(params)
    for (const [key, value] of Object.entries(updates)) {
      if (value) next.set(key, value)
      else next.delete(key)
    }
    setParams(next)
  }

  return (
    <section className="container-shell page-section menu-page">
      <div className="menu-heading">
        <h1>Our menu</h1>
        <form
          key={query}
          className="menu-search"
          onSubmit={(event) => {
            event.preventDefault()
            const value = new FormData(event.currentTarget).get('q')
            updateFilters({
              q: typeof value === 'string' ? value.trim() : '',
              page: '',
            })
          }}
        >
          <label className="search-field">
            <span className="sr-only">Search menu</span>
            <span className="glyph glyph-search" aria-hidden="true" />
            <input
              type="search"
              name="q"
              defaultValue={query}
              maxLength={120}
              placeholder="Search the menu"
            />
          </label>
          <button type="submit" className="button button-primary">
            Search
          </button>
        </form>
      </div>
      {categories.resource?.error && (
        <RequestError
          message="Categories are unavailable."
          retry={categories.reload}
        />
      )}
      {categories.resource?.data && (
        <div className="category-filters" role="group" aria-label="Categories">
          {[{ id: '', name: 'All items' }, ...categories.resource.data].map(
            (category) => (
              <button
                key={category.id}
                type="button"
                aria-pressed={categoryId === String(category.id)}
                onClick={() =>
                  updateFilters({ categoryId: String(category.id), page: '' })
                }
                className="chip"
              >
                {category.name}
              </button>
            ),
          )}
        </div>
      )}
      <h2 className="sr-only">Products</h2>
      {!products.resource && (
        <p className="catalog-status" role="status">
          Loading menu…
        </p>
      )}
      {products.resource?.error && (
        <RequestError
          message="The menu is unavailable right now."
          retry={products.reload}
        />
      )}
      {products.resource?.data && (
        <>
          {products.resource.data.items.length ? (
            <ProductGrid
              products={products.resource.data.items}
              menuSearch={params.toString()}
            />
          ) : (
            <p className="catalog-status" role="status">
              {page > 0
                ? 'No items on this page.'
                : 'No items match your search.'}
            </p>
          )}
          {(page > 0 || products.resource.data.totalPages > 1) && (
            <div
              className="flex flex-wrap gap-3"
              role="group"
              aria-label="Menu pages"
            >
              <button
                type="button"
                className="button button-outline"
                disabled={page === 0}
                onClick={() =>
                  updateFilters({ page: page > 1 ? String(page - 1) : '' })
                }
              >
                Previous page
              </button>
              <button
                type="button"
                className="button button-outline"
                disabled={page + 1 >= products.resource.data.totalPages}
                onClick={() => updateFilters({ page: String(page + 1) })}
              >
                Next page
              </button>
            </div>
          )}
        </>
      )}
    </section>
  )
}
