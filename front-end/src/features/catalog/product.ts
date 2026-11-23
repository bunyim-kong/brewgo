export type Product = {
  id: number
  slug: string
  name: string
  description: string | null
  allergens: string | null
  imageUrl: string | null
  basePrice: string
  currency: string
}

export type ProductPage = { items: Product[]; totalPages: number }

export function formatPrice(product: Product) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: product.currency,
  }).format(Number(product.basePrice))
}
