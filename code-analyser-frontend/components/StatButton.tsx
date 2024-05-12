import Link from "next/link";

export default function StatButton() {
    return (
        <>
        <Link href="/stat" className="py-2 px-4 rounded-md no-underline bg-btn-background hover:bg-btn-background-hover">
        Statistics
      </Link>
      </>
    );
  }