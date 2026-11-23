export default function RequestError({
  message,
  retry,
}: {
  message: string
  retry: () => void
}) {
  return (
    <div className="surface flex flex-wrap items-center justify-between gap-4 p-5">
      <p role="alert">{message}</p>
      <button className="button button-outline" type="button" onClick={retry}>
        Try again
      </button>
    </div>
  )
}
