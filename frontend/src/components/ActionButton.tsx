function ActionButton({ text }: { text: string }) {
  return (
    <button className="px-4 py-2 text-compl-300 border-2 border-compl-300 control">{text}</button>
  )
}

export default ActionButton