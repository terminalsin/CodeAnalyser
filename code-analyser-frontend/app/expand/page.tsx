import Link from "next/link";
import { headers } from "next/headers";
import { createClient } from "@/utils/supabase/server";
import { redirect } from "next/navigation";
import RectangleComp from "@/components/RectangleComp";

export default function CodeAnalyze(){
    return (
        <>
        <h1 className="font-bold text-4xl my-10">Codeme.java</h1>
        <div className="flex flex-row w-2/3 max-h-screen justify-center content-center">
            <div className="flex max-h-60 max-w-2/3 p-96 bg-yellow-500 rounded-lg mr-10"> </div>
            <div className="flex max-h-full p-48 max-w-2/5 bg-green-500 rounded-lg"> </div>
        </div>
        </>
    )
}
