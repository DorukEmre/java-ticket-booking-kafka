function ActionButton({ text }: { text: string }) {
  return (
    <button className="px-4 py-2 bg-back-300 text-compl-300 border-2 border-compl-300">{text}</button>
  )
}

export default ActionButton