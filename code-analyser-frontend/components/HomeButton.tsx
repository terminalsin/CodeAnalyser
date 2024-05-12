import Link from "next/link"
export default function HomeButton() {
    return (
        <div className="flex-grow">
        <Link href="/protected" className="self-start py-2 px-4 rounded-md no-underline bg-btn-background hover:bg-btn-background-hover">
            Home
        </Link>
        </div>
    )
}