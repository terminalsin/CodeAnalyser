export default function Header() {
  return (
    <div className="flex flex-col gap-6 h-80 bg-blue-700 justify-self-center items-center w-full">
      <div className="flex gap-8 justify-center items-center ">
        <h1 className="gap-6 flex font-bold text-6xl justify-center items-center my-20">Code Analyzer</h1>
      </div>
      <div className="w-full p-[4px] bg-gradient-to-r from-transparent via-foreground/10 to-transparent my-1" />
    </div>
  );
}
