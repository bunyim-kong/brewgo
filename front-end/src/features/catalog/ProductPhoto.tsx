import { useState } from 'react'
import americano from '../../assets/images/americano.webp'
import croissant from '../../assets/images/butter-croissant.webp'
import cappuccino from '../../assets/images/cappuccino.webp'
import icedLatte from '../../assets/images/iced-latte.webp'
import matchaLatte from '../../assets/images/hot-matcha-latte.webp'
import fallback from '../../assets/images/menu-fallback.webp'
import type { Product } from './product'

const photos = new Map([
  ['americano', americano],
  ['butter-croissant', croissant],
  ['cappuccino', cappuccino],
  ['iced-latte', icedLatte],
  ['matcha-latte', matchaLatte],
])

export default function ProductPhoto({
  product,
  detail = false,
}: {
  product: Product
  detail?: boolean
}) {
  const [failedSource, setFailedSource] = useState<string>()
  const local = photos.get(product.slug)
  const source =
    product.imageUrl && product.imageUrl !== failedSource
      ? product.imageUrl
      : local

  return (
    <img
      className="product-photo"
      src={source || fallback}
      alt={detail && source ? product.name : ''}
      width={508}
      height={508}
      loading={detail ? 'eager' : 'lazy'}
      decoding="async"
      referrerPolicy="no-referrer"
      onError={() => {
        if (product.imageUrl && source === product.imageUrl) {
          setFailedSource(product.imageUrl)
        }
      }}
    />
  )
}
